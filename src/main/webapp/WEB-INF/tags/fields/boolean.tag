<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="fd" type="me.bgx.budget.util.FieldDescription" %>

<div class="col-sm-5">
    <label class="checkbox-inline"><form:checkbox path="${fd.path}" id="fd-${fd.path}" /> ${fd.additionalInformation}</label>
</div>
