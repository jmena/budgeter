<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib tagdir="/WEB-INF/tags/fields" prefix="fields" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="fd" type="me.bgx.budget.util.FieldDescription" %>

<spring:bind path="${fd.path}">
    <c:set var="showError" value="${status.error}" />
</spring:bind>
<div class="form-group ${showError ? 'has-error' : ''}">

    <form:label cssClass="col-sm-2 control-label" path="${fd.path}">${fd.label}</form:label>

    <c:choose>
        <c:when test="${fd.type == 'boolean'}">
            <fields:boolean fd="${fd}" />
        </c:when>
        <c:when test="${fd.type == 'String' or
                        fd.type == 'int' or
                        fd.type == 'percentage' or
                        fd.type == 'double' or
                        fd.type == 'nperiods' or
                        fd.type == 'Period'
                        }">
            <fields:simpletext fd="${fd}"/>
        </c:when>
        <c:when test="${fd.type == 'textarea'}">
            <fields:textarea fd="${fd}" />
        </c:when>
        <c:when test="${fd.type == 'LocalDate'}">
            <fields:localdate fd="${fd}" />
        </c:when>
        <c:when test="${fd.type == 'tags'}">
            <fields:tags fd="${fd}" />
        </c:when>
        <c:otherwise>
            <div class="col-sm-2">
                <form:input cssClass="form-control" path="${fd.path}" /> - ${fd.type}
            </div>
        </c:otherwise>
    </c:choose>

</div>
