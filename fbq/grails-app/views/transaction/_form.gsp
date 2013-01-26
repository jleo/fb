<%@ page import="org.Transaction" %>



<div class="fieldcontain ${hasErrors(bean: transactionInstance, field: 'bet', 'error')} required">
	<label for="bet">
		<g:message code="transaction.bet.label" default="Bet" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="bet" type="number" value="${transactionInstance.bet}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: transactionInstance, field: 'betInfo', 'error')} ">
	<label for="betInfo">
		<g:message code="transaction.betInfo.label" default="Bet Info" />
		
	</label>
	<g:textField name="betInfo" value="${transactionInstance?.betInfo}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: transactionInstance, field: 'clientId', 'error')} required">
	<label for="clientId">
		<g:message code="transaction.clientId.label" default="Client Id" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="clientId" type="number" value="${transactionInstance.clientId}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: transactionInstance, field: 'delta', 'error')} required">
	<label for="delta">
		<g:message code="transaction.delta.label" default="Delta" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="delta" value="${fieldValue(bean: transactionInstance, field: 'delta')}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: transactionInstance, field: 'matchId', 'error')} ">
	<label for="matchId">
		<g:message code="transaction.matchId.label" default="Match Id" />
		
	</label>
	<g:textField name="matchId" value="${transactionInstance?.matchId}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: transactionInstance, field: 'resultRA', 'error')} required">
	<label for="resultRA">
		<g:message code="transaction.resultRA.label" default="Result RA" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="resultRA" type="number" value="${transactionInstance.resultRA}" required=""/>
</div>

<div class="fieldcontain ${hasErrors(bean: transactionInstance, field: 'resultRB', 'error')} required">
	<label for="resultRB">
		<g:message code="transaction.resultRB.label" default="Result RB" />
		<span class="required-indicator">*</span>
	</label>
	<g:field name="resultRB" type="number" value="${transactionInstance.resultRB}" required=""/>
</div>

