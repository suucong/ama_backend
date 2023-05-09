$(document).ready(function() {
    const updateUserBtn = document.querySelector('#updateUserBtn');
    const cancelBtn = document.querySelector('#cancelBtn');

    updateUserBtn.addEventListener('click', async () => {
        const idInput = document.querySelector('#id');
        const nameInput = document.querySelector('#username');
        const introduceInput = document.querySelector('#introduce');
        const instaIdInput = document.querySelector('#instaId');
        const pictureInput = document.querySelector('#picture');
        const imgFileInput = document.querySelector('#imgFile');

        var formData = new FormData();
        formData.append("imgFile", imgFileInput.files[0]);

        var data = {
            info: {
                id: idInput.value,
                name: nameInput.value,
                introduce: introduceInput.value,
                instaId: instaIdInput.value,
            }
        }

        formData.append(
            "requestDto",
            new Blob([JSON.stringify(data.info)], {type: "application/json"})
        );

        console.log(formData);

        try {
            $.ajax({
                type: "PUT",
                url: `/spaces/user/update/${idInput.value}`,
                processData: false,
                contentType: false,
                data: formData,
                success: function (response) {
                    alert('수정되었습니다!');
                },
                error: function (request, status, error) {
                    alert(error);
                    location.href='/';
                }
            });
        } catch (error) {
            console.error(error);
        }
    });

    cancelBtn.addEventListener('click', async () => {
        try {
            alert('유저 정보 수정이 취소되었습니다.');
            location.href = '/';
        } catch (error) {
            console.error(error);
        }
    });
});
