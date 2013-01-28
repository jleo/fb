package org

import grails.converters.JSON
import org.springframework.dao.DataIntegrityViolationException

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
        def matchInstance = Match.findByMatchIdAndCid(params.matchId,params.cid)
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

        if (cid && cid != "-1" && matchId) {
            def count = Match.countByMatchIdAndCid(matchId, cid)
            render(view: 'list', model: [query: [cid: cid, matchId: matchId],matchInstanceList: Match.findAllByMatchIdAndCid(matchId, params), matchInstanceTotal: count])
        } else if (cid && cid != -1 && !matchId) {
            def count = Match.countByCid(cid)
            render(view: 'list', model: [query: [cid: cid],matchInstanceList: Match.findAllByCid(cid, params), matchInstanceTotal: count])
        }else if((!cid || cid == -1) && !matchId){
            def count = Match.count()
            render(view: 'list', model: [query: [cid: cid],matchInstanceList: Match.findAll(params), matchInstanceTotal: count])
        }
        else {
            def count = Match.countByMatchId(matchId)
            render(view: 'list', model: [query: [cid: cid, matchId: matchId], matchInstanceList: Match.findAllByMatchId(matchId, params), matchInstanceTotal: count])
        }
    }

    def mongo
    def showCharts(){
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        params.skip = params.skip?params.int('skip'):0
        if (!params.team)
            params.team = "Manchester United"
        def db = mongo.getDB("fb")
        params.sort = 'time'
        params.order = "desc"
        def ss1
        println params.max
        if (!params.visiting){
            ss1 = db.result.find([tNameA:params.team ,cid:'18']).sort([time:-1]).skip(params.skip).limit(params.max)
        } else{
            ss1 = db.result.find([tNameB:params.team ,cid:'18']).sort([time:-1]).skip(params.skip).limit(params.max)
        }

//
        def r1=[]
        def r2=[]
        def times =[]
        def rs =[]
          ss1.each{
//            it.time = it.time.time
                def score = "${it.resultRA}:${it.resultRB}"
            r1<<[teamA:it.tNameA[2],teamB:it.tNameB[2],y:it.resultRA,x: it.time.time,Score:score]
            r2<<[teamA:it.tNameA[2],teamB:it.tNameB[2],y:it.resultRB,x: it.time.time,Score:score]



            rs << [teamA:it.tNameA[2],teamB:it.tNameB[2],x: it.time.time, y: params.visiting?it.resultRB-it.resultRA:it.resultRA-it.resultRB ,Score: score]
            times << it.time.time
        }
         r1.sort{it.x}.reverse()
         r2.sort{it.x}.reverse()
        println r1
        println r2

//        [matchInstanceJson: Match.list(params) as JSON, matchInstanceTotal: Match.count()]
        [ where:params.visiting?"客场":"主场",team:params.team  ,teamAGoals:r1 as JSON, rs: rs as JSON , teamBGoals: r2 as JSON]
    }
}
