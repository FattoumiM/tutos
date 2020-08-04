/*
$('document').ready(function () {
    $('.table .btn-info').on('click', function (event) {
        event.preventDefault();
        var href = $(this).attr('href');
        $.get(href, function (user, status) {
            $('#idInput').val(user.id);
            $('#usernameInput').val(user.username);
            $('#firstNameInput').val(user.firstName);
            $('#lastNameInput').val(user.lastName);
            $('#passwordInput').val(user.password);
        });
        $('#editModal').modal();
    });
    $('.table #deleteButton').on('click', function (event) {
        event.preventDefault();
        var href = $(this).attr('href');
        $.get(href, function (user, status) {
            $('#idDel').val(user.id);
            $('#usernameDel').val(user.username);
            $('#firstNameDel').val(user.firstName);
            $('#lastNameDel').val(user.lastName);
            $('#passwordDel').val(user.password);
        });
        $('#deleteModal').modal();
    });
});
*/
/*
$(document).ready(function(){

$('.table .delBtn').on('click', function(event) {
    event.preventDefault();
    var href = $(this).attr('href');
    $('#removeModalCenter #delRef').attr('href', href);
    $('#removeModalCenter').modal();
});
});*/
