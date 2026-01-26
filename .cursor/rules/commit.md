### Commit guidelines

- Use Conventional Commits with a clear, capitalized summary after the type.
- Prefer present-tense, imperative summaries.
- Keep the subject under ~70 characters and avoid trailing periods.
- Use the body to explain the "why" when the change is non-trivial.

# Format

Format: `<type>(<scope>): <subject>`

`<scope>` is optional

## Example

```
feat: Add coffee input screen
^--^  ^---------------------^
|     |
|     +-> Summary in present tense.
|
+-------> Type: chore, docs, feat, fix, refactor, style, or test.
```

More Examples:

- `feat`: (new feature for the user, not a new feature for build script)
- `fix`: (bug fix for the user, not a fix to a build script)
- `docs`: (changes to the documentation)
- `style`: (formatting, missing semi colons, etc; no production code change)
- `refactor`: (refactoring production code, eg. renaming a variable)
- `test`: (adding missing tests, refactoring tests; no production code change)
- `chore`: (updating grunt tasks etc; no production code change)
