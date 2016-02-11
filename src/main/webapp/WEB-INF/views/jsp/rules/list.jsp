<deco:container>

    <div class="page-header">
        <h1>Rules</h1>
    </div>

    <c:forEach var="rule" items="${rules}">

        <div class="panel panel-default">
            <div class="panel-body">
                <div class="col-md-11">
                    <a href="/app/rules/${rule.type}/${rule.id}">
                        <h4>${(empty rule.name) ? '<i>no name</i>' : rule.name}</h4>
                    </a>
                    <span>${rule.label}</span>

                </div>
                <div class="col-md-1 text-right">
                    <span class="glyphicon glyphicon-trash" aria-hidden="true"></span>
                </div>
            </div>
        </div>

    </c:forEach>


    <a href="/app/rules/new"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> New rule...</a>

</deco:container>
