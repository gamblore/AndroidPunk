
var TIMER = 0.15;

var text = "This is from a script!"
var pos = 0;

var curTime = 0.0;

function apply(entity) {
	pos += 1;
	while (text.charAt(pos) == " ") {
		pos += 1;
	}
	
	entity.setText(text.substr(0, pos));
}

function update(entity) {
	curTime += FP.elapsed;
	if (curTime > TIMER) {
		apply(entity);
		curTime -= TIMER;
	}
	
	if (pos == text.length) {
		pos = 0;
	}
}