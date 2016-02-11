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
    <c:forEach var="error" items="${errors}">

        <div class="alert alert-danger" role="alert">
                <%--<span class="glyphicon glyphicon-warning-sign" aria-hidden="true"></span>--%>
                <%--<strong>Warning</strong>--%>
                <%--Couldn't find the rule. Creating a new one.--%>
            <strong>Error</strong>
            in field '${error.field}', invalid value '${error.rejectedValue}'. ${error}
        </div>

    </c:forEach>

    <form:form modelAttribute="rule" action="/rules/${rule.type}/new" cssClass="form-horizontal">

        <form:hidden path="id" />

        <c:forEach var="fieldDescription" items="${fields}">
            <deco:input type="${fieldDescription.type}" path="${fieldDescription.path}" label="${fieldDescription.label}" rule="${rule}" />
        </c:forEach>

        <button type="submit" class="btn btn-primary">Save</button>
        <a href="/rules">Go back</a>
    </form:form>


</deco:container>
