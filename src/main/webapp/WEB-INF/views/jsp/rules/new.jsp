<deco:container>
    <div class="page-header">
        <h1>Create new rule</h1>
    </div>


    <form action="/rules/" method="POST">


        <c:forEach var="e" items="${editors}" varStatus="vs">
            <c:set var="editor" value="${e.value}" />
            <div class="radio">
                <label><input type="radio" name="type" value="${editor.ruleType}" ${vs.first ? 'checked' : ''}>${editor.label}</label>
            </div>

        </c:forEach>
        <button type="submit" class="btn btn-primary">New rule</button>
    </form>


</deco:container>
