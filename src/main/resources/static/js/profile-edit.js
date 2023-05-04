$(document).ready(function() {

const updateUserBtn = document.querySelector('#updateUserBtn');
const cancelBtn = document.querySelector('#cancelBtn');


updateUserBtn.addEventListener('click', async () => {
  const idInput=document.querySelector('#id');
  const nameInput = document.querySelector('#username');
  const introduceInput = document.querySelector('#introduce');
  const pictureInput = document.querySelector('#picture');
  const instaIdInput = document.querySelector('#instaId');

  const updateRequest = {
    id: idInput.value,
    name: nameInput.value,
    introduce: introduceInput.value,
    picture: pictureInput.value,
    instaId: instaIdInput.value
  };

  try {
    const response = await fetch(`/spaces/user/update/${idInput.value}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(updateRequest)
    });

    if (!response.ok) {
      const errorMessage = await response.text();
      console.error(errorMessage);
      return;
    }

    alert('유저 정보가 수정되었습니다.');
    location.href = '/';
  } catch (error) {
    console.error(error);
  }
});

cancelBtn.addEventListener('click', async () => {

try{
    alert('유저 정보 수정이 취소되었습니다.');
    location.href = '/';
  } catch (error) {
    console.error(error);
  }
});

});

