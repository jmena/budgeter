<deco:container>
    <div class="page-header">
        <h1>Projects</h1>
    </div>

    <c:forEach var="projectRef" items="${registeredUser.projects}">
        <c:set var="projectName" value="${projectRef.value.name}" />
        <c:if test="${empty projectName}">
            <i>No name</i>
        </c:if>
        <div class="row">
            <div class="col-sm-12">
                <a href="/app/projects/${projectRef.value.id}">${projectName}</a>
            </div>
        </div>
    </c:forEach>

    <div class="row">
        <div class="col-sm-12">
            <a href="/app/projects/new"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> New project...</a>
        </div>
    </div>

</deco:container>
