<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="path" type="java.lang.String" required="true" %>
<%@ attribute name="label" type="java.lang.String" required="true" %>
<%@ attribute name="type" type="java.lang.String" %>
<%@ attribute name="showError" type="java.lang.Boolean" %>
<%@ attribute name="rule" type="me.bgx.budget.model.v1.Rule" %>

<%--<div class="form-group">--%>
<%--<label for="${path}">${label}</label>--%>
<%--<form:label path="${path}">${label}</form:label>--%>
<%--<form:input path="${path}" cssClass="form-control" id="${path}" placeholder="${label}" />--%>
<%--<div class="has-error">--%>
<%--<form:errors path="${path}" class="help-inline"/>--%>
<%--</div>--%>
<%--</div>--%>

<div class="form-group ${showError ? 'has-error' : ''}">

    <c:choose>
        <c:when test="${type == 'boolean'}">
            <div class="col-sm-2"></div>

        </c:when>
        <c:otherwise>
            <form:label cssClass="col-sm-2 control-label" path="${path}">${label}</form:label>
        </c:otherwise>
    </c:choose>
    <c:choose>
        <c:when test="${type == 'boolean'}">
            <div class="col-sm-3">
                <label class="checkbox-inline"><form:checkbox path="${path}"/> ${label}</label>
            </div>
        </c:when>
        <c:when test="${type == 'LocalDate'}">
            <c:set var="divId" value="date-picker-${path}-id" />
            <div class="col-sm-2">
                <div class="input-group date">
                    <input name="${path}" type="text" class="form-control" id="${divId}" value="${rule[path]}">
                        <span class="input-group-addon"><i class="glyphicon glyphicon-th"></i></span>
                    </input>
                </div>
                <script>
                    $(document).ready(function () {
                        $('#${divId}').datepicker({
                            format: "yyyy-m-d",
                            maxViewMode: 0,
                            autoclose: true,
                            todayHighlight: true
                        });
                    });
                </script>
            </div>
        </c:when>
        <c:when test="${type == 'String' or type == 'int' or type == 'percentage' && type == 'double'}">
            <div class="col-sm-2">
                <form:input cssClass="form-control" type="text" path="${path}" />
            </div>
        </c:when>
        <c:when test="${type == 'textarea'}">
            <div class="col-sm-5">
                <form:textarea rows="5" cssClass="form-control" path="${path}" />
            </div>
        </c:when>
        <c:when test="${type == 'tags'}">
            <c:set var="divId" value="date-picker-${path}-id" />
            <div class="col-sm-5">
                <form:input path="${path}" cssClass="form-control" id="${divId}" />
            </div>
            <script>
                $(document).ready(function () {
                            $('#${divId}').tokenfield()
                });
            </script>
        </c:when>
        <c:otherwise>
            <div class="col-sm-2">
                <form:input cssClass="form-control" type="${empty type ? 'text' : type}" path="${path}"/> - ${type}
            </div>
        </c:otherwise>

    </c:choose>

    <%--<span class="help-block">Please correct the error</span>--%>
    <%--<form:errors path="${path}" delimiter="">--%>
    <%--<c:set var="errorClass" value="has-error"/>--%>
    <%--&lt;%&ndash;<div class="col-md-1">&ndash;%&gt;--%>
    <%--&lt;%&ndash;<div class="inline-form alert alert-danger" role="alert">&ndash;%&gt;--%>
    <%--&lt;%&ndash;error&ndash;%&gt;--%>
    <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
    <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
    <%--</form:errors>--%>

</div>
