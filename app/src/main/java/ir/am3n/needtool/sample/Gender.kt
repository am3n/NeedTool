package ir.am3n.needtool.sample

enum class Gender(var code: Int) {

    MALE(1) {
        override fun stringFa(): String = "آقا"
    },

    FEMALE(2) {
        override fun stringFa(): String = "خانم"
    };

    abstract fun stringFa(): String

    companion object {
        fun get(code: Int?): Gender {
            values().forEach { rc ->
                if (rc.code == code)
                    return rc
            }
            return MALE
        }
    }

}