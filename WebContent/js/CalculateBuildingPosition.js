
/* Work in Progress */ 

$(document).ready(function() {
   
	$(".foo").keyup(function () {
		
		var patt = new RegExp(",");
		var res = patt.test(this.value);
	
		if (res) {

			var value = $(this).val().replace(',','');
			alert(value);
    		$(this).text( value );

      		var next = $(this).parent().nextAll().children(".foo").eq(0).focus();
      		
      		next.select();
		}	
	});
});

console.log("loaded jQuery script for input");
