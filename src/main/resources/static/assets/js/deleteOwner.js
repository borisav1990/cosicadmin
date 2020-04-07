$(document).ready(function(){
	$('.delBtn').on('click', function(event){
		event.preventDefault();
     var name = $(this).data('owner');
     console.log(name);
     var href= $(this).attr('href');
         $('#myModal #deleteLearner').text("Želite da obrišete vlasnika "+ name);
		 $('#myModal #delRef').attr('href',href);
		 $('#myModal').modal();
		 
	});
	}); 
