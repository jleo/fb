
<%@ page import="org.Bet" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'bet.label', default: 'Bet')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-bet" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-bet" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list bet">
			
				<g:if test="${betInstance?.aid}">
				<li class="fieldcontain">
					<span id="aid-label" class="property-label"><g:message code="bet.aid.label" default="Aid" /></span>
					
						<span class="property-value" aria-labelledby="aid-label"><g:fieldValue bean="${betInstance}" field="aid"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${betInstance?.bet}">
				<li class="fieldcontain">
					<span id="bet-label" class="property-label"><g:message code="bet.bet.label" default="Bet" /></span>
					
						<span class="property-value" aria-labelledby="bet-label"><g:fieldValue bean="${betInstance}" field="bet"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${betInstance?.betOn}">
				<li class="fieldcontain">
					<span id="betOn-label" class="property-label"><g:message code="bet.betOn.label" default="Bet On" /></span>
					
						<span class="property-value" aria-labelledby="betOn-label"><g:fieldValue bean="${betInstance}" field="betOn"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${betInstance?.cid}">
				<li class="fieldcontain">
					<span id="cid-label" class="property-label"><g:message code="bet.cid.label" default="Cid" /></span>
					
						<span class="property-value" aria-labelledby="cid-label"><g:fieldValue bean="${betInstance}" field="cid"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${betInstance?.clientId}">
				<li class="fieldcontain">
					<span id="clientId-label" class="property-label"><g:message code="bet.clientId.label" default="Client Id" /></span>
					
						<span class="property-value" aria-labelledby="clientId-label"><g:fieldValue bean="${betInstance}" field="clientId"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${betInstance?.matchId}">
				<li class="fieldcontain">
					<span id="matchId-label" class="property-label"><g:message code="bet.matchId.label" default="Match Id" /></span>
					
						<span class="property-value" aria-labelledby="matchId-label"><g:fieldValue bean="${betInstance}" field="matchId"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${betInstance?.matchTime}">
				<li class="fieldcontain">
					<span id="matchTime-label" class="property-label"><g:message code="bet.matchTime.label" default="Match Time" /></span>
					
						<span class="property-value" aria-labelledby="matchTime-label"><g:formatDate date="${betInstance?.matchTime}" /></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${betInstance?.id}" />
					<g:link class="edit" action="edit" id="${betInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
