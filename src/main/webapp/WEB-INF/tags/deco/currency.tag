<%@ tag import="java.text.DecimalFormat" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="value" type="java.lang.Double" required="true" %>

<c:set var="isNegative" value="${value < 0}" />
<%--<% boolean isNegative = value.compareTo(0.0) < 0; %>--%>
<span class="budget-currency ${isNegative ? 'budget-negative' : ''}">

    <%--// String.format(Locale.US, "%d", value)--%>
    <%
        boolean isNegative = (value < 0);
        if (isNegative) {
            out.write("(");
            value = -value;
        }
        DecimalFormat df = new DecimalFormat("#,##0.0#");
        df.setMaximumFractionDigits(0);
        out.write(df.format(value));
        if (isNegative) {
            out.write(")");
        } else {
            out.write("&nbsp;");
        }
    %>
    <%--<c:choose>--%>
        <%----%>
        <%--<c:when test="${isNegative}">--%>
            <%--(${-value})--%>
        <%--</c:when>--%>
        <%--<c:otherwise>--%>
            <%--${value}--%>
        <%--</c:otherwise>--%>
    <%--</c:choose>--%>

<%

//    NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);;
//    if (isNegative) {
//        out.write('(');
//        value = value.negate();
//    }
//    out.write(formatter.format(value));
//    if (isNegative) {
//        out.write(')');
//    } else {
//        out.write("&nbsp");
//    }
%>
</span>
