var TestScriptEntity = Packages.com.gamblore.androidpunk.test.scripts.TestScriptEntity;
function main(world) {
	var e = new TestScriptEntity("This is from a Script!");
	e.x = 10;
	e.y = 10;
	world.add(e);
	return 0;
}