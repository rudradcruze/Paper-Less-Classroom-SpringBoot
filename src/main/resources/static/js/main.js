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

    },

        $(".content-richText").richText()

    );
    
    $('.btn-tab-next').on('click',function(e){
        e.preventDefault();
        $('.nav-tabs .nav-item > .active').parent().next('li').find('a').trigger('click');
    });
    $('.custom-file input[type="file"]').on('change', function(){
        var filename = $(this).val().split('\\').pop();
        $(this).next().text(filename);
    });
});

    new MultiSelectTag('countries')  // id


    function clickToInviteCode(copyText) {

        // const copyText = document.getElementById(element);

        // console.log(copyText);
        // Select the text field
        copyText.select();
        copyText.setSelectionRange(0, 99999); // For mobile devices

        // // Copy the text inside the text field
        navigator.clipboard.writeText(copyText.value);
        console.log(copyText.value);
    }

    function toggleCheckboxes() {
        var checkboxes = document.getElementsByName("sections");
        var selectAllCheckbox = document.getElementById("selectAll");

        for (var i = 0; i < checkboxes.length; i++) {
            checkboxes[i].checked = selectAllCheckbox.checked;
        }
    }