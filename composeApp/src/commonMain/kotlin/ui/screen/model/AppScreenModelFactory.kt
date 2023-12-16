package ui.screen.model

interface AppScreenModelFactory <ScreenModel: AppScreenModel> {
    fun build(): ScreenModel
}
