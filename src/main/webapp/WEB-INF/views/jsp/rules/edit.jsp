<deco:container>
    <div class="page-header">
        <h1>Rule editor</h1>
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

    <form:form modelAttribute="rule" cssClass="form-horizontal">
        <form:hidden path="id" />
        <deco:input path="name" label="Name" />
        <deco:input path="from" label="From" />
        <deco:input path="to" label="To" />
        <deco:input path="period" label="Period" />
        <deco:input path="amount" label="Amount" />

        <button type="submit" class="btn btn-primary">Save</button>
        <a href="/rules">Go back</a>
    </form:form>


</deco:container>
