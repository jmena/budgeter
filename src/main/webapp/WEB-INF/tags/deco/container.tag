<%@ taglib tagdir="/WEB-INF/tags/deco" prefix="deco" %>
<%@ attribute name="cssClass" type="java.lang.String" %>

<deco:html>
<div class="${(empty cssClass) ? 'container' : cssClass}">
<jsp:doBody/>
</div><!--container-->
</deco:html>
