let form = document.getElementById("classworkPost");
let url = form.querySelector('input[name="url"]').value;

var assignmentButton = document.getElementById("assignment").onclick = function () {
    form.action = `/classroom/${url}?type=ASSIGNMENT`;
}

var materialButton = document.getElementById("material").onclick = function () {
    form.action = `/classroom/${url}?type=MATERIAL`;
}

var quizButton = document.getElementById("quiz").onclick = function () {
    form.action = `/classroom/${url}?type=QUIZ`;
}