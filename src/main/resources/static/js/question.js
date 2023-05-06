$(document).ready(function() {

const updateUserBtn = document.querySelector('#updateBtn');
const cancelBtn = document.querySelector('#cancelBtn');
const currentSpaceId = $('#receivingSpaceId').val();
const sendingUserName=document.querySelector('#sendingUserName');
const sentUserPics= $('#sentUserPic').val();
console.log(currentSpaceId);
console.log(currentSpaceId.value);


updateUserBtn.addEventListener('click', async () => {
  const questionInput = document.querySelector('#questionText');

  const QuestionDTO = {
  questionText: questionInput.value,
  sentUserPic: sentUserPics,
  userId: sendingUserName.value,
  isAnonymous : false
  };

  try {
    const response = await fetch(`/spaces/${currentSpaceId}/question/create`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(QuestionDTO)
    });

    if (!response.ok) {
      const errorMessage = await response.text();
      console.error(errorMessage);
      return;
    }

    alert('질문이 등록되었습니다.');
    location.href = '/spaces/'+currentSpaceId;
  } catch (error) {
    console.error(error);
  }
});

cancelBtn.addEventListener('click', async () => {

try{
    alert('질문 등록이 취소되었습니다.');
    location.href = '/spaces/'+currentSpaceId;
  } catch (error) {
    console.error(error);
  }
});

});

