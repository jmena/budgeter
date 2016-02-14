function deleteRule(id, callback) {
    $.ajax({
        url: '/app/rules/' + id,
        type: 'DELETE',
        success: function(result) {
            $("#rule-panel-" + id).fadeOut();
        }
    });
}
