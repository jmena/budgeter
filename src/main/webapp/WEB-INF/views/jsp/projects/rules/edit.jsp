<deco:container>
    <div class="page-header">
        <h1>Edit rule - ${rule.label}</h1>
    </div>


    <c:if test="${empty rule.id}">
        <div class="alert alert-warning" role="alert">
            <%--<span class="glyphicon glyphicon-warning-sign" aria-hidden="true"></span>--%>
            <%--<strong>Warning</strong>--%>
            <%--Couldn't find the rule. Creating a new one.--%>
            Creating a new rule.
        </div>
    </c:if>
    <c:if test="${param.saved || saved}">
        <div class="alert alert-success" role="alert">
            Saved successfully
        </div>
    </c:if>
    <c:if test="${hasErrors}">
        <div class="alert alert-danger" role="alert">
            <span class="glyphicon glyphicon-error-sign" aria-hidden="true"></span>
            <%--<strong>Warning</strong>--%>
            <%--Couldn't find the rule. Creating a new one.--%>
            <strong>Error</strong> in one or more of the fields.
        </div>
    </c:if>

    <form:form modelAttribute="rule" action="/app/projects/${projectId}/rules/${rule.type}/${rule.id}" cssClass="form-horizontal">

        <form:hidden path="id" />

        <c:forEach var="fieldDescription" items="${fields}">
            <fields:field fd="${fieldDescription}" />
        </c:forEach>

        <button type="submit" class="btn btn-primary">Save</button>
        <a href="/app/projects/${projectId}">Go back</a>
    </form:form>


</deco:container>
