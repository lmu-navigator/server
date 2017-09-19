var scrollPosition = 0;

/**
 * Cross iFrame-communication code
 */
$(function() {
	$('.position').click( function() {
		scrollToIFrame();

		// get the Code of the floor to be updated
		var floorCode = $(this).closest("tr").attr( "floor" );
		document.getElementById("TileViewer").contentWindow.positionRoomManually(floorCode);
	} );

	function positionManually() {
		document.getElementById("TileViewer").contentWindow.positionRoomManually(floorCode);
		scrollToIFrame();
	}
});

/**
 * Abort manual positioning
 */
$(document).keyup(function(e) {
	if (e.keyCode == 27) {		// ESC
		document.getElementById("TileViewer").contentWindow.positionManually = false;
		scrollToTop();
	}
});


/**
 * Update the coordinates of the room
 * @param roomCode	room to update
 * @param x			new x-coordinate for the room
 * @param y			new y-coordinate for the room
 */
function updatedRoomPosition(roomCode, x, y) {
	// update X value
	$("input[name='lat-"+roomCode+"']").val(x).removeClass("missing")
	 .addClass("highlight");

	// update Y value
	$("input[name='lng-"+roomCode+"']").val(y).removeClass("missing")
	.addClass("highlight");

	scrollToTop();
}

/**
 * Scroll animations
 */
function scrollToTop() {
	$('html, body').animate({
        scrollTop: scrollPosition
    }, 500);
}

function scrollToIFrame() {
	scrollPosition = document.body.scrollTop;
	$('html, body').animate({
        scrollTop: $('iframe').offset().top
    }, 500);
}
