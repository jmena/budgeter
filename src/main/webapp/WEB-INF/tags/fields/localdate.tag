<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="fd" type="me.bgx.budget.util.FieldDescription" %>

<div class="col-sm-3">
    <spring:bind path="${fd.path}">
        <div class="input-group date">
            <input name="${fd.path}" type="text" class="form-control" id="fd-${fd.path}" value="${status.value}">
                <span class="input-group-addon"><i class="glyphicon glyphicon-th"></i></span>
            </input>
        </div>
    </spring:bind>
    <script>
        $(document).ready(function () {
            $('#fd-${fd.path}').datepicker({
                format: "yyyy-m-d",
                maxViewMode: 0,
                autoclose: true,
                todayHighlight: true
            });
        });
    </script>
</div>
