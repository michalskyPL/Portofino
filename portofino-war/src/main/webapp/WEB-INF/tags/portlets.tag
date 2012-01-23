<%@ attribute name="list" required="true" %>
<%@ attribute name="cssClass" required="false" %>
<%@ taglib prefix="stripes" uri="http://stripes.sourceforge.net/stripes-dynattr.tld" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="actionBean" scope="request"
             type="com.manydesigns.portofino.actions.PortletAction"/>
<div class="${cssClass} portletContainer">
    <input type="hidden" name="portletWrapperName_${list}" value="portletWrapper_${list}" />
    <c:forEach var="portletInstance" items="${ actionBean.portlets[list] }">
        <div class="portletWrapper" id="portletWrapper_<c:out value='${portletInstance.id}' />">
            <jsp:include page="${portletInstance.jsp}" />
        </div>
    </c:forEach>
</div>