
RCCOBJS=$(BUILDDIR)/alloc.o \
	$(BUILDDIR)/bind.o \
	$(BUILDDIR)/dag.o \
	$(BUILDDIR)/dagcheck.o \
	$(BUILDDIR)/decl.o \
	$(BUILDDIR)/enode.o \
	$(BUILDDIR)/error.o \
	$(BUILDDIR)/expr.o \
	$(BUILDDIR)/event.o \
	$(BUILDDIR)/init.o \
	$(BUILDDIR)/inits.o \
	$(BUILDDIR)/input.o \
	$(BUILDDIR)/lex.o \
	$(BUILDDIR)/list.o \
	$(BUILDDIR)/main.o \
	$(BUILDDIR)/output.o \
	$(BUILDDIR)/prof.o \
	$(BUILDDIR)/profio.o \
	$(BUILDDIR)/simp.o \
	$(BUILDDIR)/stmt.o \
	$(BUILDDIR)/string.o \
	$(BUILDDIR)/sym.o \
	$(BUILDDIR)/trace.o \
	$(BUILDDIR)/tree.o \
	$(BUILDDIR)/types.o \
	$(BUILDDIR)/null.o \
	$(BUILDDIR)/symbolic.o \
	$(BUILDDIR)/gen.o \
	$(BUILDDIR)/bytecode.o \
	$(BUILDDIR)/alpha.o \
	$(BUILDDIR)/mips.o \
	$(BUILDDIR)/eco32.o \
	$(BUILDDIR)/sparc.o \
	$(BUILDDIR)/stab.o \
	$(BUILDDIR)/x86.o \
	$(BUILDDIR)/x86linux.o

$(BUILDDIR)/alloc.o:	src/alloc.c;			gcc -g -c -Isrc -o $@ src/alloc.c
$(BUILDDIR)/bind.o:		src/bind.c;				gcc -g -c -Isrc -o $@ src/bind.c
$(BUILDDIR)/dag.o:		src/dag.c;				gcc -g -c -Isrc -o $@ src/dag.c
$(BUILDDIR)/decl.o:		src/decl.c;				gcc -g -c -Isrc -o $@ src/decl.c
$(BUILDDIR)/enode.o:	src/enode.c;			gcc -g -c -Isrc -o $@ src/enode.c
$(BUILDDIR)/error.o:	src/error.c;			gcc -g -c -Isrc -o $@ src/error.c
$(BUILDDIR)/event.o:	src/event.c;			gcc -g -c -Isrc -o $@ src/event.c
$(BUILDDIR)/expr.o:		src/expr.c;				gcc -g -c -Isrc -o $@ src/expr.c
$(BUILDDIR)/gen.o:		src/gen.c;				gcc -g -c -Isrc -o $@ src/gen.c
$(BUILDDIR)/init.o:		src/init.c;				gcc -g -c -Isrc -o $@ src/init.c
$(BUILDDIR)/inits.o:	src/inits.c;			gcc -g -c -Isrc -o $@ src/inits.c
$(BUILDDIR)/input.o:	src/input.c;			gcc -g -c -Isrc -o $@ src/input.c
$(BUILDDIR)/lex.o:		src/lex.c;				gcc -g -c -Isrc -o $@ src/lex.c
$(BUILDDIR)/list.o:		src/list.c;				gcc -g -c -Isrc -o $@ src/list.c
$(BUILDDIR)/main.o:		src/main.c;				gcc -g -c -Isrc -o $@ src/main.c
$(BUILDDIR)/null.o:		src/null.c;				gcc -g -c -Isrc -o $@ src/null.c
$(BUILDDIR)/output.o:	src/output.c;			gcc -g -c -Isrc -o $@ src/output.c
$(BUILDDIR)/prof.o:		src/prof.c;				gcc -g -c -Isrc -o $@ src/prof.c
$(BUILDDIR)/profio.o:	src/profio.c;			gcc -g -c -Isrc -o $@ src/profio.c
$(BUILDDIR)/simp.o:		src/simp.c;				gcc -g -c -Isrc -o $@ src/simp.c
$(BUILDDIR)/stmt.o:		src/stmt.c;				gcc -g -c -Isrc -o $@ src/stmt.c
$(BUILDDIR)/string.o:	src/string.c;			gcc -g -c -Isrc -o $@ src/string.c
$(BUILDDIR)/sym.o:		src/sym.c;				gcc -g -c -Isrc -o $@ src/sym.c
$(BUILDDIR)/symbolic.o:	src/symbolic.c;			gcc -g -c -Isrc -o $@ src/symbolic.c
$(BUILDDIR)/bytecode.o:	src/bytecode.c;			gcc -g -c -Isrc -o $@ src/bytecode.c
$(BUILDDIR)/trace.o:	src/trace.c;			gcc -g -c -Isrc -o $@ src/trace.c
$(BUILDDIR)/tree.o:		src/tree.c;				gcc -g -c -Isrc -o $@ src/tree.c
$(BUILDDIR)/types.o:	src/types.c;			gcc -g -c -Isrc -o $@ src/types.c
$(BUILDDIR)/stab.o:		src/stab.c src/stab.h;	gcc -g -c -Isrc -o $@ src/stab.c

$(BUILDDIR)/dagcheck.o:	$(BUILDDIR)/dagcheck.c;	gcc -g -c -Isrc -o $@ $(BUILDDIR)/dagcheck.c
$(BUILDDIR)/alpha.o:	$(BUILDDIR)/alpha.c;	gcc -g -c -Isrc -o $@ $(BUILDDIR)/alpha.c
$(BUILDDIR)/mips.o:	$(BUILDDIR)/mips.c;	gcc -g -c -Isrc -o $@ $(BUILDDIR)/mips.c
$(BUILDDIR)/eco32.o:	$(BUILDDIR)/eco32.c;	gcc -g -c -Isrc -o $@ $(BUILDDIR)/eco32.c
$(BUILDDIR)/sparc.o:	$(BUILDDIR)/sparc.c;	gcc -g -c -Isrc -o $@ $(BUILDDIR)/sparc.c
$(BUILDDIR)/x86.o:	$(BUILDDIR)/x86.c;	gcc -g -c -Isrc -o $@ $(BUILDDIR)/x86.c
$(BUILDDIR)/x86linux.o:	$(BUILDDIR)/x86linux.c;	gcc -g -c -Isrc -o $@ $(BUILDDIR)/x86linux.c

$(BUILDDIR)/dagcheck.c:	$(BUILDDIR)/lburg src/dagcheck.md; $(BUILDDIR)/lburg src/dagcheck.md $@
$(BUILDDIR)/alpha.c:	$(BUILDDIR)/lburg src/alpha.md;    $(BUILDDIR)/lburg src/alpha.md    $@
$(BUILDDIR)/mips.c:	$(BUILDDIR)/lburg src/mips.md;     $(BUILDDIR)/lburg src/mips.md     $@
$(BUILDDIR)/eco32.c:	$(BUILDDIR)/lburg src/eco32.md;    $(BUILDDIR)/lburg src/eco32.md    $@
$(BUILDDIR)/sparc.c:	$(BUILDDIR)/lburg src/sparc.md;    $(BUILDDIR)/lburg src/sparc.md    $@
$(BUILDDIR)/x86.c:	$(BUILDDIR)/lburg src/x86.md;      $(BUILDDIR)/lburg src/x86.md      $@
$(BUILDDIR)/x86linux.c:	$(BUILDDIR)/lburg src/x86linux.md; $(BUILDDIR)/lburg src/x86linux.md $@
