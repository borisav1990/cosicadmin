$(document).ready(function(){
	$('.delBtnVeh').on('click', function(event){
		event.preventDefault();
     var name = $(this).data('vehicle');
     console.log(name);
     var href= $(this).attr('href');
         $('#myModal #deleteLearner').text("Brišete vozilo "+ name +  " ?");
		 $('#myModal #delRef').attr('href',href);
		 $('#myModal').modal();
		 });
	}); 