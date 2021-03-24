package com.nachc.dba

class SearchScreenViewModelTest {

    /*@get:Rule
    var rule = InstantTaskExecutorRule() // allows to execute a task and instantly get a response (synchronously)

    @Mock
    lateinit var routeRepository: RouteRepository

    //val application = Mockito.mock(Application::class.java)

    var searchScreenViewModel = SearchScreenViewModel()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        DaggerSearchViewModelComponent.builder()
            .firebaseModule(FirebaseModuleTest(routeRepository))
            .build()
            .inject(searchScreenViewModel)
    }

    @Test
    fun validateInputEmptyBlank() {
        val input = ""
        val expectedValidInputValue = Pair(false, "Enter a bus line")
        val expectedLoadingValue = false

        searchScreenViewModel.validateInput(input)

        Assert.assertEquals(expectedValidInputValue, searchScreenViewModel.validInput.value)
        Assert.assertEquals(expectedLoadingValue, searchScreenViewModel.loading.value)
    }

    @Test
    fun validateInputCharacters() {
        val input = "66%b"
        val expectedValidInputValue = Pair(false, "Only letters & numbers")
        val expectedLoadingValue = false

        searchScreenViewModel.validateInput(input)

        Assert.assertEquals(expectedValidInputValue, searchScreenViewModel.validInput.value)
        Assert.assertEquals(expectedLoadingValue, searchScreenViewModel.loading.value)
    }

    @Test
    fun validateInputSuccess() {
        val input = "66b"
        val expectedValidInputValue = Pair(true, "")

        searchScreenViewModel.validateInput(input)

        Assert.assertEquals(expectedValidInputValue, searchScreenViewModel.validInput.value)
    }

    @Test
    fun searchSuccess() {
        val input = "66b"
        val trip = Trip("id", null, null, null, null, null)
        val trips = listOf(trip)
        val testSingle = Single.just(trips)
        Mockito.`when`(routeRepository.getRouteData(input)).thenReturn(testSingle)

        searchScreenViewModel.search(input)

        Assert.assertEquals(1, searchScreenViewModel.trips.value?.size)
        Assert.assertEquals(false, searchScreenViewModel.loading.value)
        Assert.assertEquals(Pair(false, ""), searchScreenViewModel.loadError.value)
    }

    @Test
    fun searchFailure() {
        val input = "66b"
        val e = Throwable("error message")
        val testSingle = Single.error<List<Trip>>(e)

        Mockito.`when`(routeRepository.getRouteData(input)).thenReturn(testSingle)

        searchScreenViewModel.search(input)

        Assert.assertEquals(null, searchScreenViewModel.trips.value)
        Assert.assertEquals(false, searchScreenViewModel.loading.value)
        Assert.assertEquals(true, searchScreenViewModel.loadError.value?.first)
    }

    @Before
    fun setupRxSchedulers() {
        val immediate = object: Scheduler() {
            override fun createWorker(): Worker {
                return ExecutorScheduler.ExecutorWorker({ it.run() }, true, true)
            }
        }

        RxJavaPlugins.setInitNewThreadSchedulerHandler { immediate }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { immediate }
    }*/
}