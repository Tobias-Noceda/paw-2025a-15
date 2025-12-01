<script lang="ts">
  import { cn } from '$lib/utils';
  import DefaultPfp from '$assets/images/default-pfp.png';

  interface Props {
    src?: string;
    size?: 'sm' | 'md' | 'lg' | 'xl' | 'auto';
    class?: string;
  }

  let {
    src = '',
    size = 'md',
    class: avatarClass
  }: Props = $props();

  let currentSrc = $state(src);
  let hasError = $state(false);

  $effect(() => {
    currentSrc = src;
    hasError = false;
  });

  function handleError() {
    if (!hasError) {
      hasError = true;
    }
  }

  const sizeStyles = {
    sm: 'width: 22px; height: 22px;',
    md: 'width: 40px; height: 40px;',
    lg: 'width: 60px; height: 60px;',
    xl: 'width: 84px; height: 84px;',
    auto: 'width: 100%; height: 100%;',
  };

  const imgStyle = "width: 100%; height: 100%; object-fit: cover; border-radius: 50%; overflow: hidden; " + sizeStyles[size];

  const finalClass = cn(
    avatarClass
  );
</script>

<div class={finalClass} style="padding: 2px; border-radius: 50%; {sizeStyles[size]}">
  {#if !src || hasError}
    <img
      src={DefaultPfp}
      alt="Default Avatar"
      style="width: 100%; height: 100%; object-fit: cover; border-radius: 50%; display: block;"
    />
  {:else}
    <img
      src={currentSrc}
      alt="Avatar"
      style="width: 100%; height: 100%; object-fit: cover; border-radius: 50%; display: block;"
      onerror={handleError}
    />
  {/if}
</div>