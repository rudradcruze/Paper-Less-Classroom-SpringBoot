"use strict"

$(window).on("load", function() {
    $('.btn-forget').on('click',function(e){
        e.preventDefault();
        var inputField = $(this).closest('form').find('input');
        if(inputField.attr('required') && inputField.val()){
            $('.error-message').remove();
            $('.form-items','.form-content').addClass('hide-it');
            $('.form-sent','.form-content').addClass('show-it');
        }else{
            $('.error-message').remove();
            $('<small class="error-message">Please fill the field.</small>').insertAfter(inputField);
        }

    });
    
    $('.btn-tab-next').on('click',function(e){
        e.preventDefault();
        $('.nav-tabs .nav-item > .active').parent().next('li').find('a').trigger('click');
    });
    $('.custom-file input[type="file"]').on('change', function(){
        var filename = $(this).val().split('\\').pop();
        $(this).next().text(filename);
    });
});

    function clickToInviteCode() {
        // Get the text field
        const copyText = document.getElementById("copyInviteCode");

        // Select the text field
        copyText.select();
        copyText.setSelectionRange(0, 99999); // For mobile devices

        // Copy the text inside the text field
        navigator.clipboard.writeText(copyText.value);
        console.log(copyText.value);
    }

    // document.getElementById("test").onclick = function() {myFunction()};
    //
    // function myFunction() {
    //     // Get the text field
    //     const copyText = document.getElementById("copyInviteCode");
    //
    //     // Select the text field
    //     copyText.select();
    //     copyText.setSelectionRange(0, 99999); // For mobile devices
    //
    //     // Copy the text inside the text field
    //     navigator.clipboard.writeText(copyText.value);
    //     console.log(copyText.value);
    // }
