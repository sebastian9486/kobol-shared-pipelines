#!groovy

/**
 * Build UI Bundle
 *
 * param ...
 */
def build(bundle) {
    cd src/main/antora/$1/ui
    gulp bundle
    cd ../../../../../

    echo "${bundle}"
}

def uploadSnapshot() {
    echo "dfgdf"
}

def uploadRelease() {
    echo "dfgdf dffdgf"
}
