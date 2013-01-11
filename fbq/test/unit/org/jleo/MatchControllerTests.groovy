package org.jleo



import org.junit.*
import grails.test.mixin.*

@TestFor(MatchController)
@Mock(Match)
class MatchControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/match/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.matchInstanceList.size() == 0
        assert model.matchInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.matchInstance != null
    }

    void testSave() {
        controller.save()

        assert model.matchInstance != null
        assert view == '/match/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/match/show/1'
        assert controller.flash.message != null
        assert Match.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/match/list'


        populateValidParams(params)
        def match = new Match(params)

        assert match.save() != null

        params.id = match.id

        def model = controller.show()

        assert model.matchInstance == match
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/match/list'


        populateValidParams(params)
        def match = new Match(params)

        assert match.save() != null

        params.id = match.id

        def model = controller.edit()

        assert model.matchInstance == match
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/match/list'

        response.reset()


        populateValidParams(params)
        def match = new Match(params)

        assert match.save() != null

        // test invalid parameters in update
        params.id = match.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/match/edit"
        assert model.matchInstance != null

        match.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/match/show/$match.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        match.clearErrors()

        populateValidParams(params)
        params.id = match.id
        params.version = -1
        controller.update()

        assert view == "/match/edit"
        assert model.matchInstance != null
        assert model.matchInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/match/list'

        response.reset()

        populateValidParams(params)
        def match = new Match(params)

        assert match.save() != null
        assert Match.count() == 1

        params.id = match.id

        controller.delete()

        assert Match.count() == 0
        assert Match.get(match.id) == null
        assert response.redirectedUrl == '/match/list'
    }
}
