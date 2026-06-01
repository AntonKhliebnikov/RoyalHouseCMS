$(function () {
    initAboutCompanyEditor();
    initBannerPreview();
});

function initAboutCompanyEditor() {
    var editor = $('.about-company-editor');

    if (!editor.length || !$.fn.summernote) {
        return;
    }

    editor.summernote({
        height: 260,
        toolbar: [
            ['font', ['bold', 'italic', 'underline', 'clear']],
            ['para', ['ul', 'ol', 'paragraph']],
            ['view', ['codeview']]
        ]
    });
}

function initBannerPreview() {
    var input = document.querySelector('[data-banner-input]');
    var preview = document.querySelector('[data-banner-preview]');

    if (!input || !preview) {
        return;
    }

    input.addEventListener('change', function () {
        var file = input.files && input.files[0];

        if (!file) {
            return;
        }

        if (!file.type || !file.type.startsWith('image/')) {
            return;
        }

        var reader = new FileReader();

        reader.onload = function (event) {
            preview.innerHTML = '';

            var image = document.createElement('img');
            image.src = event.target.result;
            image.alt = 'Предпросмотр баннера';

            preview.appendChild(image);
        };

        reader.readAsDataURL(file);
    });
}