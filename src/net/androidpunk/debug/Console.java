package net.androidpunk.debug;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.android.PunkActivity;

import org.apache.http.conn.util.InetAddressUtils;

import android.util.Log;

public class Console {
	private static final String TAG = "Console";
	
	private ServerSocket mListeningSocket;
	
	private static final Map<String, Command> mCommands = new HashMap<String, Command>();
	private static final SortedSet<String> mSortedCommands = new TreeSet<String>();
	
	private boolean mRunning = true;
	
	public class ConsoleThread implements Runnable {
		private Socket mSock;
		private InputStream mInputStream;
		private OutputStream mOutputStream;
		
		private final byte[] data = new byte[4096];
		private int dataIndex = 0;
		
		public ConsoleThread(Socket sock) {
			mSock = sock;
			
			try {
				mInputStream = mSock.getInputStream();
				mOutputStream = mSock.getOutputStream();
				mOutputStream.write('>');
			} catch (IOException e) {
				e.printStackTrace();
				mSock = null;
				return;
			}
			
		}
		
		public void shutdown() {
			try {
				mSock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mSock = null;
		}
		
		public void run() {
			while (mSock != null) {
				if (!mRunning) {
					shutdown();
					continue;
				}
				try {
					String output = handleInput();
					
					if (output != null) {
						mOutputStream.write(output.getBytes());
						mOutputStream.write('\r');
						mOutputStream.write('\n');
						mOutputStream.write('>');
						mOutputStream.write(' ');
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) { }
			}
		}
		
		private String handleInput() throws IOException {
			while (mInputStream.available() > 0) {
				//process the thing
				int character = mInputStream.read();
				if (dataIndex > data.length) {
					int value;
					while ((value = mInputStream.read()) != '\n' || value != -1);
					if (value == -1) {
						mSock.close();
						mSock = null;
					}
					return "Command is too long.";
				}
				if (character == -1) {
					// Disconnected; exit.
					mSock.close();
					mSock = null;
				} else {
					data[dataIndex++] = (byte) character;
					
					if (dataIndex > 2 && data[dataIndex-2] == '\r' && data[dataIndex-1] == '\n') {
						String s = new String(data,0,dataIndex-2);
						dataIndex = 0;
						
						String cmd;
						String[] parts = null;
						
						int argsIndex = s.indexOf(" ");
						if (argsIndex != -1) {
							cmd = s.substring(0, argsIndex);
							parts = s.substring(argsIndex+1).split(" ");
						} else {
							cmd = s;
						}
						
						//execute
						Command c = mCommands.get(cmd);
						if (c != null) {
							synchronized (PunkActivity.mUpdateLock) {
								return c.execute(parts);
							}
							
						} else {
							return "Command: '" + cmd + "' is not a valid command."; 
						}
					}
				}
			}
			return null;
		}
	}
	
	private String getIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
	                    return inetAddress.getHostAddress();
	                }
	            }
	        }
	    } catch (SocketException ex) {}
	    return null;
	}
	
	/**
	 * Creates a listening socket that talks telnet for commands.
	 */
	public Console() {
		
		setupDefaultCommands();
		
		try {
			ServerSocket s = new ServerSocket();
			s.setReuseAddress(true);
			InetSocketAddress addr = new InetSocketAddress(31337);
			s.bind(addr);
			mListeningSocket = s;
		} catch (IOException e) {
			e.printStackTrace();
		};
		
		Log.i(TAG, String.format("Console is up on %s:31337", getIpAddress()));
		
		if (mListeningSocket != null) {
			Thread acceptor = new Thread(new Runnable() {
				
				public void run() {
					while(mRunning) {
						try {
							Socket s = mListeningSocket.accept();
							new Thread(new ConsoleThread(s)).start();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			});
			acceptor.start();
		}
	}
	
	public void shutdown() {
		mRunning = false;
		if (mListeningSocket != null) {
			try {
				mListeningSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Adds some basic functions.
	 * help - display commands available.
	 * tags - display a list of tags and entity details (Calls entity toString()).
	 * count - displays the number of entities in the current world.
	 * pause - toggle pausing of the update loop (render loop still runs).
	 * step - update the loop by 16ms.
	 */
	private void setupDefaultCommands() {
		Command help = new Command() {
			@Override
			public String execute(String... args) {
				String s = "[Commands]\r\n";
				for (String cmd : mSortedCommands) {
					s += cmd + "\r\n";
				}
				return s;
			}
		};
		registerCommand("help", help);
		
		Command tags = new Command() {
			@Override
			public String execute(String... args) {
				String s = "";
				Vector<Entity> entities = new Vector<Entity>();
				
				World w = FP.getWorld();
				String[] types = w.getTypes();
				
				for (int i = 0; i < types.length; i++) {
					String entitiesString = "";
					w.getType(types[i], entities);
					
					for (int j = 0; j < entities.size(); j++) {
						Entity e = entities.get(j);
						entitiesString += "\t" + e.toString() + "\r\n";
					}
					
					s += String.format("%s:%s\r\n", types[i], entitiesString);
					entities.clear();
				}
				return s;
			}
		};
		registerCommand("tags", tags);
		
		Command count = new Command() {
			@Override
			public String execute(String... args) {
				return String.format("Entity Count: %d\r\n", FP.getWorld().getCount());
			}
		};
		registerCommand("count", count);
		
		Command pause = new Command() {
			@Override
			public String execute(String... args) {
				FP.engine.paused = !FP.engine.paused;
				return String.format("paused: %b\r\n", FP.engine.paused);
			}
		};
		registerCommand("pause", pause);
		
		Command step = new Command() {
			@Override
			public String execute(String... args) {
				float elapsed = 0.016f;
				try {
					int i = Integer.parseInt(args[0]);
					elapsed = (float)i/1000f;
				} catch (Exception e) {}
				FP.elapsed = elapsed;
				FP.getWorld().update();
				return String.format("Stepped %.4f seconds\r\n", elapsed);
			}
		};
		registerCommand("step", step);
	}
	
	/**
	 * Add a command to the console.
	 * @param name the name of the command.
	 * @param c The command to attach to it.
	 */
	public static void registerCommand(String name, Command c) {
		mSortedCommands.add(name);
		mCommands.put(name, c);
	}
	
	/**
	 * Remove a command from the console.
	 * @param name the name of the list of commands.
	 */
	public static void removeCommand(String name) {
		mSortedCommands.remove(name);
		mCommands.remove(name);
	}
	
	/**
	 * Logs data to the console.
	 * @param	...data		The data parameters to log, can be variables, objects, etc. Parameters will be separated by a space (" ").
	 */
	public void log(Object... data) {
		String s;
		if (data.length > 1)
		{
			s = "";
			int i = 0;
			while (i < data.length)
			{
				if (i > 0) 
					s += " ";
				s += data[i++].toString();
			}
		}
		else 
			s = data[0].toString();
		
		if (s.indexOf("\n") >= 0) {
			String[] a = s.split("\n");
			for (String string : a) {
				Log.d(TAG, string);
			}
		}
	}
}
