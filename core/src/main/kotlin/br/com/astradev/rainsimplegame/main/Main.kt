package br.com.astradev.rainsimplegame.main

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.Texture.TextureFilter.Linear
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxGame
import ktx.app.KtxScreen
import ktx.assets.disposeSafely
import ktx.assets.toInternalFile
import ktx.async.KtxAsync

class Main : KtxGame<KtxScreen>() {
    override fun create() {
        KtxAsync.initiate()
        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}

class FirstScreen : KtxScreen {
    private val backgroundTexture: Texture = Texture("images/background.png".toInternalFile(), true)
        .apply { setFilter(Linear, Linear) }
    private val bucketTexture: Texture = Texture("images/bucket.png".toInternalFile(), true)
        .apply { setFilter(Linear, Linear) }
    private val dropTexture: Texture = Texture("images/drop.png".toInternalFile(), true)
        .apply { setFilter(Linear, Linear) }

    val dropSound: Sound = Gdx.audio.newSound(Gdx.files.internal("music/drop.mp3"))
    val music: Music = Gdx.audio.newMusic(Gdx.files.internal("music/music.mp3"))

    private val image = Texture("logo.png".toInternalFile(), true)
        .apply { setFilter(Linear, Linear) }
    private val batch = SpriteBatch()
    private val viewport = FitViewport(5f,8f)

    private val bucketSprite = Sprite(bucketTexture)
    private var touchPos: Vector2

    private var dropSprites : MutableList<Sprite>

    private var dropTimer: Float = 0.0f

    private var bucketRectangle: Rectangle
    private var dropletRectangle: Rectangle

    init {
        bucketSprite.setSize(1f , 1f)
        touchPos = Vector2()
        dropSprites = emptyArray<Sprite>().toMutableList()
        bucketRectangle = Rectangle()
        dropletRectangle = Rectangle()
        music.isLooping = true
        music.volume = 0.5f
        music.play()
        createDroplet()
    }

    override fun render(delta: Float) {
        input()
        logic()
        draw()
    }

    override fun dispose() {
        image.disposeSafely()
        batch.disposeSafely()
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true) // true centers the camera
    }

    private fun createDroplet(){
        var dropWidth = 1f
        var dropheight = 1f
        var worldWidth = viewport.worldWidth
        var worldHeight = viewport.worldHeight

        var dropSprite = Sprite(dropTexture)
        dropSprite.setSize(dropWidth, dropheight)
        dropSprite.x = MathUtils.random(0f, worldWidth - dropWidth)
        dropSprite.y = worldHeight

        dropSprites.add(dropSprite)
    }

    private fun input(){
        if(Gdx.input.isTouched){
            touchPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
            viewport.unproject(touchPos)
            bucketSprite.setCenterX(touchPos.x)
        }
    }

    private fun logic(){
        val delta = Gdx.graphics.deltaTime
        val worldWidth = viewport.worldWidth
        val worldHeight = viewport.worldHeight
        val bucketWidth = bucketSprite.width
        val bucketHeight = bucketSprite.height

        bucketSprite.setX(MathUtils.clamp(bucketSprite.x, 0f, worldWidth - bucketWidth))
        bucketRectangle.set(bucketSprite.x, bucketSprite.y, bucketWidth, bucketHeight)

        for (i in dropSprites.size - 1 downTo 0) {
            val dropSprite = dropSprites[i]
            val dropWidth = dropSprite.width
            val dropHeight = dropSprite.height

            dropSprite.translateY(-2f * delta)
            dropletRectangle.set(dropSprite.x, dropSprite.y, dropWidth, dropHeight)


            if (dropSprite.y < -dropHeight) dropSprites.removeAt(i)
            else if (bucketRectangle.overlaps(dropletRectangle)) {
                dropSprites.removeAt(i)
                dropSound.play()
            }
        }

        dropTimer += delta
        if (dropTimer > 1f) {
            dropTimer = 0f
            createDroplet()
        }
    }

    private fun draw(){
        ScreenUtils.clear(Color.DARK_GRAY)
        viewport.apply()
        batch.projectionMatrix = viewport.camera.combined

        batch.begin()

        val worldWidth = viewport.worldWidth
        val worldHeight = viewport.worldHeight

        //batch.draw(backgroundTexture, 0f, 0f, worldWidth, worldHeight)

        bucketSprite.draw(batch)

        for (droplet in dropSprites){
            droplet.draw(batch)
        }

        batch.end()
    }

}
