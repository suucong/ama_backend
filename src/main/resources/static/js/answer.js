$(document).ready(function() {
const updateUserBtn = document.querySelector('#updateBtn');
const cancelBtn = document.querySelector('#cancelBtn');
const currentSpaceId = $('#spaceId').val();
const questionId_= $('#questionId').val();
const userId_= $('#userId').val();

const sendingUserName = document.querySelector('#sendingUserName').textContent;
const sentUserPics = document.querySelector('#sentUserPic').getAttribute('src');



updateUserBtn.addEventListener('click', async () => {
  const answerInput = document.querySelector('#answerText');
  if (answerInput.value.trim() === '') {
    alert('답변 내용을 입력하세요.');
    return;
  }
const isPublic_ = (document.getElementById("isPublic").value == "📢 공개 답변") ? true : false;


const AnswerDTO = {
  answerText: answerInput.value,
  sentUserPic: sentUserPics,
  userId: userId_,
  userName: sendingUserName,
  isPublic: isPublic_,
  questionId:questionId_
};

console.log(AnswerDTO);


  try {
    const response = await fetch(`/spaces/${currentSpaceId}/${questionId_}/answer/create`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(AnswerDTO)
    });

    if (!response.ok) {
      const errorMessage = await response.text();
      console.error(errorMessage);
      return;
    }

    alert('답변이 등록되었습니다.');
    location.href = '/spaces/'+currentSpaceId;
  } catch (error) {
    console.error(error);
  }
});

cancelBtn.addEventListener('click', async () => {
console.log("취소버튼누름");
try{
    alert('답변 등록이 취소되었습니다.');
    location.href = '/spaces/'+currentSpaceId;
  } catch (error) {
    console.error(error);
  }
});

});

