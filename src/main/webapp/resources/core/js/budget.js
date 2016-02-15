function deleteRule(projectId, ruleId) {
    $.ajax({
        url: '/app/projects/' + projectId + "/rules/" + ruleId,
        type: 'DELETE',
        success: function(result) {
            $("#rule-panel-" + ruleId).fadeOut();
        }
    });
}
