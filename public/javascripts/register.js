function keyDown(e, obj, max, next) {
	if ((e.keyCode > 95 && e.keyCode < 106) || (e.keyCode > 47 && e.keyCode < 58)) {
		if (obj.value.length >= max) {
			nextObj = document.getElementById(next);
			nextObj.focus();
			nextObj.select();
		}
	}
}
function keyDownYear(event, obj) {
	keyDown(event, obj, 4, 'dob_month');
}
function keyDownMonth(event, obj) {
	keyDown(event, obj, 2, 'dob_day');
}