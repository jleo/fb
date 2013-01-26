
<%@ page import="org.Transaction" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'transaction.label', default: 'Transaction')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-transaction" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-transaction" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list transaction">
			
				<g:if test="${transactionInstance?.bet}">
				<li class="fieldcontain">
					<span id="bet-label" class="property-label"><g:message code="transaction.bet.label" default="Bet" /></span>
					
						<span class="property-value" aria-labelledby="bet-label"><g:fieldValue bean="${transactionInstance}" field="bet"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transactionInstance?.betInfo}">
				<li class="fieldcontain">
					<span id="betInfo-label" class="property-label"><g:message code="transaction.betInfo.label" default="Bet Info" /></span>
					
						<span class="property-value" aria-labelledby="betInfo-label"><g:fieldValue bean="${transactionInstance}" field="betInfo"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transactionInstance?.clientId}">
				<li class="fieldcontain">
					<span id="clientId-label" class="property-label"><g:message code="transaction.clientId.label" default="Client Id" /></span>
					
						<span class="property-value" aria-labelledby="clientId-label"><g:fieldValue bean="${transactionInstance}" field="clientId"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transactionInstance?.delta}">
				<li class="fieldcontain">
					<span id="delta-label" class="property-label"><g:message code="transaction.delta.label" default="Delta" /></span>
					
						<span class="property-value" aria-labelledby="delta-label"><g:fieldValue bean="${transactionInstance}" field="delta"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transactionInstance?.matchId}">
				<li class="fieldcontain">
					<span id="matchId-label" class="property-label"><g:message code="transaction.matchId.label" default="Match Id" /></span>
					
						<span class="property-value" aria-labelledby="matchId-label"><g:fieldValue bean="${transactionInstance}" field="matchId"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transactionInstance?.resultRA}">
				<li class="fieldcontain">
					<span id="resultRA-label" class="property-label"><g:message code="transaction.resultRA.label" default="Result RA" /></span>
					
						<span class="property-value" aria-labelledby="resultRA-label"><g:fieldValue bean="${transactionInstance}" field="resultRA"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${transactionInstance?.resultRB}">
				<li class="fieldcontain">
					<span id="resultRB-label" class="property-label"><g:message code="transaction.resultRB.label" default="Result RB" /></span>
					
						<span class="property-value" aria-labelledby="resultRB-label"><g:fieldValue bean="${transactionInstance}" field="resultRB"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${transactionInstance?.id}" />
					<g:link class="edit" action="edit" id="${transactionInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
