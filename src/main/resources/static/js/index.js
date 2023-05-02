$(document).ready(function() {
  // 보낸 질문 탭 클릭 시
  $("#sent-tab").on('click', function() {
    $("#sent-questions").show();
    $("#received-questions").hide();
    $("#sent-tab").addClass("active");
    $("#received-tab").removeClass("active");
  });

  // 받은 질문 탭 클릭 시
  $("#received-tab").on('click', function() {
    $("#received-questions").show();
    $("#sent-questions").hide();
    $("#received-tab").addClass("active");
    $("#sent-tab").removeClass("active");
  });
});
