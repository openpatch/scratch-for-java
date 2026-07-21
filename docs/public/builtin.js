/*
 * Behaviour for the generated "Built-in Sprites" and "Built-in Sounds" pages:
 * searching, copying a name to the clipboard and previewing a sound.
 *
 * Everything is bound to the document rather than to the elements themselves,
 * so it keeps working no matter when the page content appears.
 */
(function () {
  "use strict";

  var audio = null;
  var playingButton = null;

  // ------------------------------------------------------------- searching

  function applyFilter(input) {
    var query = input.value.trim().toLowerCase();
    var root = input.closest(".ba-filter").parentNode;
    var groups = root.querySelectorAll(".ba-group");
    var visible = 0;

    for (var i = 0; i < groups.length; i++) {
      var items = groups[i].querySelectorAll("[data-name]");
      var shown = 0;

      for (var j = 0; j < items.length; j++) {
        var match = query === "" || items[j].dataset.name.indexOf(query) !== -1;
        items[j].hidden = !match;
        if (match) {
          shown++;
        }
      }

      groups[i].hidden = shown === 0;
      visible += shown;
    }

    var count = root.querySelector(".ba-count");
    if (count) {
      var total = count.dataset.total;
      var noun = count.textContent.indexOf("sound") !== -1 ? "sound" : "sprite";
      count.textContent = query === ""
        ? total + " " + noun + "s"
        : visible + " of " + total + " " + noun + "s";
    }

    var empty = root.querySelector(".ba-empty");
    if (empty) {
      empty.hidden = visible !== 0;
    }
  }

  document.addEventListener("input", function (event) {
    if (event.target.classList.contains("ba-search")) {
      applyFilter(event.target);
    }
  });

  // Enter would submit a surrounding form and reload the page.
  document.addEventListener("keydown", function (event) {
    if (event.target.classList.contains("ba-search") && event.key === "Enter") {
      event.preventDefault();
    }
  });

  // -------------------------------------------------------------- copying

  function showCopied(text) {
    var toast = document.querySelector(".ba-copied");
    if (!toast) {
      toast = document.createElement("div");
      toast.className = "ba-copied";
      document.body.appendChild(toast);
    }
    toast.textContent = "Copied " + text;
    toast.dataset.visible = "true";

    window.clearTimeout(toast.hideTimer);
    toast.hideTimer = window.setTimeout(function () {
      toast.dataset.visible = "false";
    }, 1200);
  }

  function copy(text) {
    if (navigator.clipboard && navigator.clipboard.writeText) {
      navigator.clipboard.writeText(text).then(
        function () {
          showCopied(text);
        },
        function () {
          fallbackCopy(text);
        }
      );
    } else {
      fallbackCopy(text);
    }
  }

  function fallbackCopy(text) {
    var field = document.createElement("textarea");
    field.value = text;
    field.setAttribute("readonly", "");
    field.style.position = "fixed";
    field.style.opacity = "0";
    document.body.appendChild(field);
    field.select();
    try {
      document.execCommand("copy");
      showCopied(text);
    } catch (error) {
      /* Copying is a convenience; the name is visible on the page anyway. */
    }
    document.body.removeChild(field);
  }

  // -------------------------------------------------------------- playing

  function stopPlaying() {
    if (audio) {
      audio.pause();
    }
    if (playingButton) {
      playingButton.dataset.playing = "false";
      playingButton = null;
    }
  }

  function play(button) {
    var wasPlaying = playingButton === button;
    stopPlaying();
    if (wasPlaying) {
      return;
    }

    if (!audio) {
      audio = new Audio();
      audio.addEventListener("ended", stopPlaying);
      audio.addEventListener("error", stopPlaying);
    }

    audio.src = button.dataset.src;
    audio.currentTime = 0;
    var started = audio.play();
    if (started && started.catch) {
      started.catch(stopPlaying);
    }

    playingButton = button;
    button.dataset.playing = "true";
  }

  document.addEventListener("click", function (event) {
    var playButton = event.target.closest(".bs-play");
    if (playButton) {
      event.preventDefault();
      play(playButton);
      return;
    }

    var copyTarget = event.target.closest("[data-copy]");
    if (copyTarget) {
      event.preventDefault();
      copy(copyTarget.dataset.copy);
    }
  });
})();
