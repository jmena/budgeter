<deco:container>
    <div class="page-header">
        <h1>Edit project</h1>
    </div>

    <form:form modelAttribute="project" action="/app/projects/${empty project.id ? 'new' : project.id}" cssClass="form-horizontal">

        <form:hidden path="id" />

        <div class="form-group ${showError ? 'has-error' : ''}">
            <form:label cssClass="col-sm-2 control-label" path="name">Name</form:label>
            <div class="col-sm-6">
                <form:input cssClass="form-control" path="name" />
            </div>
        </div>

        <button type="submit" class="btn btn-primary">Save</button>
        <a href="/app/projects/">Go back</a>
    </form:form>


</deco:container>
