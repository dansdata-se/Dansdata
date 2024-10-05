export default {
  "!(*.kt|justfile|.justfile)": "prettier --write --ignore-unknown",
  "{justfile,.justfile}": "just --unstable --fmt --justfile",
  "*.kt": (filenames) =>
    `./gradlew ktfmtPrecommit --include-only=${filenames.map((it) => `"${it.substring(it.indexOf("src/main")).replaceAll('"', '\\"')}"`).join(",")}`,
};
