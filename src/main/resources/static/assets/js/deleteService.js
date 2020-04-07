/**
 * 
 */
$(document).ready(function(){
	$('.delBtnServ').on('click', function(event){
		event.preventDefault();
     var name = $(this).data('owner');
     var href= $(this).attr('href');
         $('#myModal #deleteLearner').text("Brišete servis vozila  "+ name + " ?");
		 $('#myModal #delRef').attr('href',href);
		 $('#myModal').modal();
		 
	});
	}); 