$(document).ready(function() {
$("#received-questions").hide();
$("#received-tab").removeClass("active");

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

const deleteBtns = document.querySelectorAll('.delete-btn');
const q_deleteBtns = document.querySelectorAll('.q-delete-btn');


q_deleteBtns.forEach(deleteBtn => {
 deleteBtn.addEventListener('click', async () => {
    console.log("삭제버튼 누름");

    const spaceId = deleteBtn.parentElement.querySelector('#spaceId').value;
    const questionId = deleteBtn.parentElement.querySelector('#questionId').value;

    console.log("questionId: " + questionId);
    console.log("spaceId: " + spaceId);

    if (confirm("정말로 질문을 삭제하시겠습니까?")) {
      // AJAX 요청 보내기
      $.ajax({
        type: 'DELETE',
        url: `/spaces/${spaceId}/${questionId}/question/delete`,
        success: function() {
          // 삭제 성공 시 결과를 동적으로 업데이트
          deleteBtn.parentElement.parentElement.remove(); // 삭제된 답변의 HTML 요소를 제거
          console.log("삭제 성공");
        },
        error: function(response) {
          // 에러 발생 시 메시지 표시
          alert(response.responseText);
          console.log("삭제 실패");
        }
      });
    }
  });
});

deleteBtns.forEach(deleteBtn => {
  deleteBtn.addEventListener('click', async () => {
    console.log("삭제버튼 누름");

    const spaceId = deleteBtn.parentElement.querySelector('#spaceId').value;
    const answerId = deleteBtn.parentElement.querySelector('#answerId').value;

    console.log("answerId: " + answerId);
    console.log("spaceId: " + spaceId);

    if (confirm("정말로 답변을 삭제하시겠습니까?")) {
      // AJAX 요청 보내기
      $.ajax({
        type: 'DELETE',
        url: `/spaces/${spaceId}/${answerId}/answer/delete`,
        success: function() {
          // 삭제 성공 시 결과를 동적으로 업데이트
          deleteBtn.parentElement.parentElement.remove(); // 삭제된 답변의 HTML 요소를 제거
          console.log("삭제 성공");
        },
        error: function(response) {
          // 에러 발생 시 메시지 표시
          alert(response.responseText);
          console.log("삭제 실패");
        }
      });
    }
  });
});



  });

