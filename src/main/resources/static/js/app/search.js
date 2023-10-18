

$('#more-view').on('click', () => {
    const keyword = window.location.search.split('=')[1];

    $.ajax({
        type: 'GET',
        url: '/posts/search',
        data: {
            query: decodeURI(keyword),
        }
    }).done((data) => {
        displaySearchResult(data);
    }).fail((error) => {
        alert(error.responseJSON.message);
        return false;
    });
});

function displaySearchResult(data) {
    const cards = $('#result-cards');

    $.each(data, function(index, result) {
        let resultCard = $('<div class="result-card"></div>').attr('data-id', result.id);

        let authorSpan = $('<span></span>').text(result.author + '    ');
        let modifiedDateSpan = $('<span style="opacity: 0.6"></span>').text(result.modifiedDate);
        let titleLink = $('<h4></h4>').append($('<a></a>').attr('href', '/posts/find/' + result.id).text(result.title));
        let contentSpan = $('<span></span>').text(result.content);
        let hr = $('<hr>');

        resultCard.append(authorSpan, modifiedDateSpan, titleLink, contentSpan, hr);
        cards.append(resultCard);
    });
}