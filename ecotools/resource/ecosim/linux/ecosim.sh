
BASEDIR=`dirname "$0"`
BINDIR=$BASEDIR/bin
LIBDIR=$BASEDIR/lib

java -cp $BINDIR:$LIBDIR/commons-cli-1.2.jar:$LIBDIR/lwjgl.jar:$LIBDIR/lwjgl_util.jar:$LIBDIR/swt.jar -Djava.library.path=$LIBDIR name.martingeisse.ecotools.simulator.ui.Main $@
