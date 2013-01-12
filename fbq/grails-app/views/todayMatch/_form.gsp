<%@ page import="org.Match" %>



<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'returnRate', 'error')} required">
	<label for="returnRate">
		<g:message code="match.returnRate.label" default="Return Rate" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="returnRate" required="" value="${fieldValue(bean: matchInstance, field: 'returnRate')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'wr', 'error')} required">
	<label for="wr">
		<g:message code="match.wr.label" default="Wr" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="wr" required="" value="${fieldValue(bean: matchInstance, field: 'wr')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'pr', 'error')} required">
	<label for="pr">
		<g:message code="match.pr.label" default="Pr" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="pr" required="" value="${fieldValue(bean: matchInstance, field: 'pr')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'lr', 'error')} required">
	<label for="lr">
		<g:message code="match.lr.label" default="Lr" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="lr" required="" value="${fieldValue(bean: matchInstance, field: 'lr')}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'matchId', 'error')} ">
	<label for="matchId">
		<g:message code="match.matchId.label" default="Match Id" />
		
	</label>
	<g:textField name="matchId" value="${matchInstance?.matchId}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'cid', 'error')} ">
	<label for="cid">
		<g:message code="match.cid.label" default="Cid" />
		
	</label>
	<g:textField name="cid" value="${matchInstance?.cid}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'w1', 'error')} ">
	<label for="w1">
		<g:message code="match.w1.label" default="W1" />
		
	</label>
	<g:textField name="w1" value="${matchInstance?.w1}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'p1', 'error')} ">
	<label for="p1">
		<g:message code="match.p1.label" default="P1" />
		
	</label>
	<g:textField name="p1" value="${matchInstance?.p1}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'l1', 'error')} ">
	<label for="l1">
		<g:message code="match.l1.label" default="L1" />
		
	</label>
	<g:textField name="l1" value="${matchInstance?.l1}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'w2', 'error')} ">
	<label for="w2">
		<g:message code="match.w2.label" default="W2" />
		
	</label>
	<g:textField name="w2" value="${matchInstance?.w2}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'p2', 'error')} ">
	<label for="p2">
		<g:message code="match.p2.label" default="P2" />
		
	</label>
	<g:textField name="p2" value="${matchInstance?.p2}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'l2', 'error')} ">
	<label for="l2">
		<g:message code="match.l2.label" default="L2" />
		
	</label>
	<g:textField name="l2" value="${matchInstance?.l2}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'mtype', 'error')} ">
	<label for="mtype">
		<g:message code="match.mtype.label" default="Mtype" />
		
	</label>
	<g:textField name="mtype" value="${matchInstance?.mtype}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'time', 'error')} ">
	<label for="time">
		<g:message code="match.time.label" default="Time" />
		
	</label>
	<g:textField name="time" value="${matchInstance?.time}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'tidA', 'error')} ">
	<label for="tidA">
		<g:message code="match.tidA.label" default="Tid A" />
		
	</label>
	<g:textField name="tidA" value="${matchInstance?.tidA}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'tNameA', 'error')} ">
	<label for="tNameA">
		<g:message code="match.tNameA.label" default="TN ame A" />
		
	</label>
	<g:textField name="tNameA" value="${matchInstance?.tNameA}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'tidB', 'error')} ">
	<label for="tidB">
		<g:message code="match.tidB.label" default="Tid B" />
		
	</label>
	<g:textField name="tidB" value="${matchInstance?.tidB}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'tNameB', 'error')} ">
	<label for="tNameB">
		<g:message code="match.tNameB.label" default="TN ame B" />
		
	</label>
	<g:textField name="tNameB" value="${matchInstance?.tNameB}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'tRankA', 'error')} ">
	<label for="tRankA">
		<g:message code="match.tRankA.label" default="TR ank A" />
		
	</label>
	<g:textField name="tRankA" value="${matchInstance?.tRankA}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'tRankB', 'error')} ">
	<label for="tRankB">
		<g:message code="match.tRankB.label" default="TR ank B" />
		
	</label>
	<g:textField name="tRankB" value="${matchInstance?.tRankB}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'resultRA', 'error')} ">
	<label for="resultRA">
		<g:message code="match.resultRA.label" default="Result RA" />
		
	</label>
	<g:textField name="resultRA" value="${matchInstance?.resultRA}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'resultRB', 'error')} ">
	<label for="resultRB">
		<g:message code="match.resultRB.label" default="Result RB" />
		
	</label>
	<g:textField name="resultRB" value="${matchInstance?.resultRB}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'resultPA', 'error')} ">
	<label for="resultPA">
		<g:message code="match.resultPA.label" default="Result PA" />
		
	</label>
	<g:textField name="resultPA" value="${matchInstance?.resultPA}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'resultPB', 'error')} ">
	<label for="resultPB">
		<g:message code="match.resultPB.label" default="Result PB" />
		
	</label>
	<g:textField name="resultPB" value="${matchInstance?.resultPB}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'wa1', 'error')} ">
	<label for="wa1">
		<g:message code="match.wa1.label" default="Wa1" />
		
	</label>
	<g:textField name="wa1" value="${matchInstance?.wa1}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'pa1', 'error')} ">
	<label for="pa1">
		<g:message code="match.pa1.label" default="Pa1" />
		
	</label>
	<g:textField name="pa1" value="${matchInstance?.pa1}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'la1', 'error')} ">
	<label for="la1">
		<g:message code="match.la1.label" default="La1" />
		
	</label>
	<g:textField name="la1" value="${matchInstance?.la1}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'wa2', 'error')} ">
	<label for="wa2">
		<g:message code="match.wa2.label" default="Wa2" />
		
	</label>
	<g:textField name="wa2" value="${matchInstance?.wa2}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'pa2', 'error')} ">
	<label for="pa2">
		<g:message code="match.pa2.label" default="Pa2" />
		
	</label>
	<g:textField name="pa2" value="${matchInstance?.pa2}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: matchInstance, field: 'la2', 'error')} ">
	<label for="la2">
		<g:message code="match.la2.label" default="La2" />
		
	</label>
	<g:textField name="la2" value="${matchInstance?.la2}"/>
</div>

