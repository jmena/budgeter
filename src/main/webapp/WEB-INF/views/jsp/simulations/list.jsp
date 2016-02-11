<deco:container>
    <div class="page-header">
        <h1>Simulations</h1>
    </div>

    <table class="table table-condensed table-hover">
        <thead>
        <tr>
            <th>Description</th>
            <c:forEach var="month" items="${months}">
                <th class="text-right">${month}</th>
            </c:forEach>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="description" items="${descriptions}">
            <tr>
                <td>
                    <c:set var="rule" value="${rulesByDescription[description]}" />
                    <a href="/rules/${rule.type}/${rule.id}">${description} <span class="glyphicon glyphicon-edit" aria-hidden="true"></span></a>
                </td>
                <c:forEach var="month" items="${months}">
                    <td class="text-right">
                        <deco:currency value="${calendar[month][description]}"/>
                    </td>
                </c:forEach>
            </tr>
        </c:forEach>
        <tfoot>
            <tr class="active">
                <th class="active">Total by month</th>
                <c:forEach var="ym" items="${months}">
                    <td class="text-right">
                        <b><deco:currency value="${totalsByMonth[ym]}"/></b>
                    </td>
                </c:forEach>
            </tr>
            <tr class="active">
                <th>Balance</th>
                <c:forEach var="ym" items="${months}">
                    <td class="text-right">
                        <b><deco:currency value="${balanceByMonth[ym]}"/></b>
                    </td>
                </c:forEach>
            </tr>
            <tr class="active">
                <th>Maximum savings by month</th>
                <c:forEach var="ym" items="${months}">
                    <td class="text-right">
                        <b><deco:currency value="${maximumSavingsByMonth[ym]}"/></b>
                    </td>
                </c:forEach>
            </tr>
        </tfoot>
        </tbody>
    </table>
</deco:container>
