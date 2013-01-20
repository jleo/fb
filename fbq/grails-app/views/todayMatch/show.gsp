
<%@ page import="org.Match" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'match.label', default: 'Match')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-match" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-match" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list match">
			
				<g:if test="${matchInstance?.returnRate}">
				<li class="fieldcontain">
					<span id="returnRate-label" class="property-label"><g:message code="match.returnRate.label" default="Return Rate" /></span>
					
						<span class="property-value" aria-labelledby="returnRate-label"><g:fieldValue bean="${matchInstance}" field="returnRate"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.wr}">
				<li class="fieldcontain">
					<span id="wr-label" class="property-label"><g:message code="match.wr.label" default="Wr" /></span>
					
						<span class="property-value" aria-labelledby="wr-label"><g:fieldValue bean="${matchInstance}" field="wr"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.pr}">
				<li class="fieldcontain">
					<span id="pr-label" class="property-label"><g:message code="match.pr.label" default="Pr" /></span>
					
						<span class="property-value" aria-labelledby="pr-label"><g:fieldValue bean="${matchInstance}" field="pr"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.lr}">
				<li class="fieldcontain">
					<span id="lr-label" class="property-label"><g:message code="match.lr.label" default="Lr" /></span>
					
						<span class="property-value" aria-labelledby="lr-label"><g:fieldValue bean="${matchInstance}" field="lr"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.matchId}">
				<li class="fieldcontain">
					<span id="matchId-label" class="property-label"><g:message code="match.matchId.label" default="Match Id" /></span>
					
						<span class="property-value" aria-labelledby="matchId-label"><g:fieldValue bean="${matchInstance}" field="matchId"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.cid}">
				<li class="fieldcontain">
					<span id="cid-label" class="property-label"><g:message code="match.cid.label" default="Cid" /></span>
					
						<span class="property-value" aria-labelledby="cid-label"><g:fieldValue bean="${matchInstance}" field="cid"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.w1}">
				<li class="fieldcontain">
					<span id="w1-label" class="property-label"><g:message code="match.w1.label" default="W1" /></span>
					
						<span class="property-value" aria-labelledby="w1-label"><g:fieldValue bean="${matchInstance}" field="w1"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.p1}">
				<li class="fieldcontain">
					<span id="p1-label" class="property-label"><g:message code="match.p1.label" default="P1" /></span>
					
						<span class="property-value" aria-labelledby="p1-label"><g:fieldValue bean="${matchInstance}" field="p1"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.l1}">
				<li class="fieldcontain">
					<span id="l1-label" class="property-label"><g:message code="match.l1.label" default="L1" /></span>
					
						<span class="property-value" aria-labelledby="l1-label"><g:fieldValue bean="${matchInstance}" field="l1"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.w2}">
				<li class="fieldcontain">
					<span id="w2-label" class="property-label"><g:message code="match.w2.label" default="W2" /></span>
					
						<span class="property-value" aria-labelledby="w2-label"><g:fieldValue bean="${matchInstance}" field="w2"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.p2}">
				<li class="fieldcontain">
					<span id="p2-label" class="property-label"><g:message code="match.p2.label" default="P2" /></span>
					
						<span class="property-value" aria-labelledby="p2-label"><g:fieldValue bean="${matchInstance}" field="p2"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.l2}">
				<li class="fieldcontain">
					<span id="l2-label" class="property-label"><g:message code="match.l2.label" default="L2" /></span>
					
						<span class="property-value" aria-labelledby="l2-label"><g:fieldValue bean="${matchInstance}" field="l2"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.mtype}">
				<li class="fieldcontain">
					<span id="mtype-label" class="property-label"><g:message code="match.mtype.label" default="Mtype" /></span>
					
						<span class="property-value" aria-labelledby="mtype-label"><g:fieldValue bean="${matchInstance}" field="mtype"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.time}">
				<li class="fieldcontain">
					<span id="time-label" class="property-label"><g:message code="match.time.label" default="Time" /></span>
					
						<span class="property-value" aria-labelledby="time-label"><g:fieldValue bean="${matchInstance}" field="time"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.tidA}">
				<li class="fieldcontain">
					<span id="tidA-label" class="property-label"><g:message code="match.tidA.label" default="Tid A" /></span>
					
						<span class="property-value" aria-labelledby="tidA-label"><g:fieldValue bean="${matchInstance}" field="tidA"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.tNameA}">
				<li class="fieldcontain">
					<span id="tNameA-label" class="property-label"><g:message code="match.tNameA.label" default="TN ame A" /></span>
					
						<span class="property-value" aria-labelledby="tNameA-label"><g:fieldValue bean="${matchInstance}" field="tNameA"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.tidB}">
				<li class="fieldcontain">
					<span id="tidB-label" class="property-label"><g:message code="match.tidB.label" default="Tid B" /></span>
					
						<span class="property-value" aria-labelledby="tidB-label"><g:fieldValue bean="${matchInstance}" field="tidB"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.tNameB}">
				<li class="fieldcontain">
					<span id="tNameB-label" class="property-label"><g:message code="match.tNameB.label" default="TN ame B" /></span>
					
						<span class="property-value" aria-labelledby="tNameB-label"><g:fieldValue bean="${matchInstance}" field="tNameB"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.tRankA}">
				<li class="fieldcontain">
					<span id="tRankA-label" class="property-label"><g:message code="match.tRankA.label" default="TR ank A" /></span>
					
						<span class="property-value" aria-labelledby="tRankA-label"><g:fieldValue bean="${matchInstance}" field="tRankA"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.tRankB}">
				<li class="fieldcontain">
					<span id="tRankB-label" class="property-label"><g:message code="match.tRankB.label" default="TR ank B" /></span>
					
						<span class="property-value" aria-labelledby="tRankB-label"><g:fieldValue bean="${matchInstance}" field="tRankB"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.wa1}">
				<li class="fieldcontain">
					<span id="wa1-label" class="property-label"><g:message code="match.wa1.label" default="Wa1" /></span>
					
						<span class="property-value" aria-labelledby="wa1-label"><g:fieldValue bean="${matchInstance}" field="wa1"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.pa1}">
				<li class="fieldcontain">
					<span id="pa1-label" class="property-label"><g:message code="match.pa1.label" default="Pa1" /></span>
					
						<span class="property-value" aria-labelledby="pa1-label"><g:fieldValue bean="${matchInstance}" field="pa1"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.la1}">
				<li class="fieldcontain">
					<span id="la1-label" class="property-label"><g:message code="match.la1.label" default="La1" /></span>
					
						<span class="property-value" aria-labelledby="la1-label"><g:fieldValue bean="${matchInstance}" field="la1"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.wa2}">
				<li class="fieldcontain">
					<span id="wa2-label" class="property-label"><g:message code="match.wa2.label" default="Wa2" /></span>
					
						<span class="property-value" aria-labelledby="wa2-label"><g:fieldValue bean="${matchInstance}" field="wa2"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.pa2}">
				<li class="fieldcontain">
					<span id="pa2-label" class="property-label"><g:message code="match.pa2.label" default="Pa2" /></span>
					
						<span class="property-value" aria-labelledby="pa2-label"><g:fieldValue bean="${matchInstance}" field="pa2"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${matchInstance?.la2}">
				<li class="fieldcontain">
					<span id="la2-label" class="property-label"><g:message code="match.la2.label" default="La2" /></span>
					
						<span class="property-value" aria-labelledby="la2-label"><g:fieldValue bean="${matchInstance}" field="la2"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${matchInstance?.id}" />
					<g:link class="edit" action="edit" id="${matchInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
