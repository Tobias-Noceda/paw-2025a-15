document.addEventListener('DOMContentLoaded', function(){

    document.querySelectorAll('form').forEach(function(form){

        form.addEventListener('submit', function(e){

            // Si tienes validaciones HTML5 y quieres que fallen

            // SIN deshabilitar, podrías hacer:

            // if (form.checkValidity && !form.checkValidity()) return;


            // Deshabilita todos los submits de ESTE formulario

            form

                .querySelectorAll('button[type=submit], input[type=submit]')

                .forEach(function(btn){

                    btn.disabled = true;

                });

            // aquí NO llamamos a e.preventDefault(): el form se sigue enviando

        });

    });

});


