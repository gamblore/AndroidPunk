var TestScriptEntity = Packages.com.gamblore.androidpunk.test.scripts.TestScriptEntity;
function main(world) {
	var e = new TestScriptEntity("");
	e.x = 10;
	e.y = 10;
	world.add(e);
	print("Added", e, "to the world.");
	return 0;
}