package org.jleo

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.mongodb.core.query.Order

class MatchController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        params.sort = 'time'
        params.order = "desc"
        [matchInstanceList: Match.list(params), matchInstanceTotal: Match.count()]
    }

    def create() {
        [matchInstance: new Match(params)]
    }

    def save() {
        def matchInstance = new Match(params)
        if (!matchInstance.save(flush: true)) {
            render(view: "create", model: [matchInstance: matchInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'match.label', default: 'Match'), matchInstance.id])
        redirect(action: "show", id: matchInstance.id)
    }

    def show() {
        def matchInstance = Match.get(params.id)
        if (!matchInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'match.label', default: 'Match'), params.id])
            redirect(action: "list")
            return
        }

        [matchInstance: matchInstance]
    }

    def edit() {
        def matchInstance = Match.get(params.id)
        if (!matchInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'match.label', default: 'Match'), params.id])
            redirect(action: "list")
            return
        }

        [matchInstance: matchInstance]
    }

    def update() {
        def matchInstance = Match.get(params.id)
        if (!matchInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'match.label', default: 'Match'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (matchInstance.version > version) {
                matchInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'match.label', default: 'Match')] as Object[],
                        "Another user has updated this Match while you were editing")
                render(view: "edit", model: [matchInstance: matchInstance])
                return
            }
        }

        matchInstance.properties = params

        if (!matchInstance.save(flush: true)) {
            render(view: "edit", model: [matchInstance: matchInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'match.label', default: 'Match'), matchInstance.id])
        redirect(action: "show", id: matchInstance.id)
    }

    def delete() {
        def matchInstance = Match.get(params.id)
        if (!matchInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'match.label', default: 'Match'), params.id])
            redirect(action: "list")
            return
        }

        try {
            matchInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'match.label', default: 'Match'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'match.label', default: 'Match'), params.id])
            redirect(action: "show", id: params.id)
        }
    }

    def query() {
        if (!params.cid && !params.matchId) {
            redirect(action: 'list', params: params)
        }
        def cid = params.cid
        def matchId = params.matchId

        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        params.sort = 'time'
        params.order = "desc"

        if (cid != "-1" && matchId) {
            def count = Match.countByMatchIdAndCid(matchId, cid)
            render(view: 'list', model: [query: [cid: cid, matchId: matchId],matchInstanceList: Match.findAllByMatchIdAndCid(matchId, params), matchInstanceTotal: count])
        } else if (cid != -1 && !matchId) {
            def count = Match.countByCid(cid)
            render(view: 'list', model: [query: [cid: cid],matchInstanceList: Match.findAllByCid(cid, params), matchInstanceTotal: count])
        }else if(cid == -1 && !matchId){
            def count = Match.count()
            render(view: 'list', model: [query: [cid: cid],matchInstanceList: Match.findAll(params), matchInstanceTotal: count])
        }
        else {
            def count = Match.countByMatchId(matchId)
            render(view: 'list', model: [query: [cid: cid, matchId: matchId], matchInstanceList: Match.findAllByMatchId(matchId, params), matchInstanceTotal: count])
        }
    }
}
